package com.aquariusinteractive

import com.aquariusinteractive.content.Taxon
import com.aquariusinteractive.content.TaxonMapping
import org.grails.plugins.csv.CSVMapReader

class ImportService {

  def taxonService

  static transactional = 'mongo'

  static String dir       = "/Users/mdbishop/Dropbox/Aquarius Interactive/Roadtrppr/Data/Refine Projects/IMPORTED/"
  static String ROUTESDIR = "/Users/mdbishop/Dropbox/Aquarius Interactive/Roadtrppr/Data/Refine Projects/RV Routes/"
  static String MAPPINGS  = "/Users/mdbishop/Dropbox/Aquarius Interactive/Michael/mapping.csv"

  /**
   * Category A, Category B, Website URL, Source,
   * Source Type, Article Name, Last Capture,
   * Date Written/Last Update, Name, Factual Response,
   * Factual First Response, Factual ID, Factual Latitude,
   * Factual Longitude, Factual City, Factual Name,
   * Factual Similarity, Factual Street Address,
   * Factual Resolved, Factual Category,
   * Factual Country, Factual Zip Code,
   * Factual Telephone, Factual Status,
   * Factual State, Address, Longitude,
   * Latitude, Source ranking, Photo,
   * Short Description, Long Description, Phone Number,
   * Rating, Expiration, Price and Free (Yes/No),
   * Name Website, Executive Chef, Signature Dish,
   * As Seen On, Campaign, Date of Occurrence,
   * Estimated Casulaties, Result, Architect, Summary, Bridge Length,
   * Year Built, Hours of Operation, Course Type, Holes
   */
  void doWork() {
    Thing.findAll()*.delete()
    def d = new File(dir)
    d.eachFile {
      log.debug(it)
      it.withReader() {  reader ->

        def r = new CSVMapReader(reader, [batchSize: 500])

        try {
          r.each() { List batchList ->
            Thing.withTransaction() { status ->
              batchList.each() {  map ->

                Thing thing = new Thing()
                try {
                  thing.name = map[ 'Name' ].toString()
                  thing.categoryA = map[ 'Category A' ].toString().intern()
                  thing.categoryB = map[ 'Category B' ].toString().intern()
                  thing.city = map[ 'Factual City' ].toString()
                  thing.factualName = map[ 'Factual Name' ].toString()
                  thing.factualCat = map[ 'Factual Category' ].toString().intern()
                  thing.factualZip = map[ 'Factual Zip Code' ].toString()
                  thing.factualState = map[ 'Factual State' ].toString().intern()
                  final double lat = map[ 'Factual Latitude' ] as Double
                  final double lon = map[ 'Factual Longitude' ] as Double
                  thing.loc = new ArrayList<Double>([lon, lat])
                  thing.factualSimilarity = map[ 'Factual Similarity' ] as Double
                  thing.save()
                }
                catch (NumberFormatException nf) {
                  log.warn(nf)
                  thing.discard()
                }
              }
            }
          }
        }
        catch (ArrayIndexOutOfBoundsException oob) {
          log.error(oob)
        }
      }
    }
  }

  Map lookupForRoutes() {
    def d = new File(ROUTESDIR)
    def results = [:]

    d.eachFile() { file ->
      file.withReader() { reader ->
        def r = new CSVMapReader(reader)

        r.initFieldKeys()
        r.each() { line ->
          log.debug("Lookup for ${line[ 'Name' ]}")
          def point = [line[ 'Longitude' ] as Double, line[ 'Latitude' ] as Double]
          def things = Thing.findByLocWithinCircle([point, 100])

          results.put(line[ 'Name' ], things)
        }
      }
    }
    return results
  }

  /**
   * Import Taxonomy mapping
   */
  void doImportInternalTaxonomy() {
    final File MAPPING_CSV_FILE = new File(MAPPINGS)

    MAPPING_CSV_FILE.withReader() { reader ->
      CSVMapReader csv = new CSVMapReader(reader, ['charset': 'UTF-8'])

      csv.initFieldKeys()

      def allMappings = csv.collect { row ->
        row[ "Internal Mapping" ].tokenize('>')*.trim()
      }
      def groupings = allMappings.groupBy {
        it[ 0 ]
      }

      log.info groupings.keySet()
      groupings.each() { groups ->
        log.debug("Processing group: ${groups}")
        groups.value.each() { record ->
          log.trace("Processing ${record}")
          taxonService.processLeaf(record)
        }
      }
      reader.close()
    }
  }

  /**
   * Import mapping file.  Expects a UTF-8 encoded
   * csv file formatted as:
   * xxx Mapping, Internal Mapping, xxx Mapping
   *
   * where:
   *   - xxx is a Provider name.
   *   - order of the columns are unimportant (But the column names are.)
   *   - Internal mapping row should be formatted as:
   *       - parent > child 1 > child 2
   *   - Provider mappings will be taken literally and should be
   *       formatted exactly as they appear in an API return call.
   */
  void doImportMapping() {

    final File MAPPING_CSV_FILE = new File(MAPPINGS)

    MAPPING_CSV_FILE.withReader() { reader ->
      CSVMapReader csv = new CSVMapReader(reader, [charset: 'UTF-8'])
      csv.initFieldKeys()
      List keys = csv.getFieldKeys()

      assert (keys.contains("Internal Mapping"))
      assert (keys.size() > 1)

      csv.each() { Map row ->
        Taxon taxon
        final List internalPathArray =
          row[ "Internal Mapping" ].tokenize('>')*.trim()
        taxon = taxonService.getOrCreateTaxonByPathArray(internalPathArray, true)
        if (!taxon) {
          throw new RuntimeException("Internal Taxon is unresolveable.  Cannot continue.\n ${internalPathArray}")
        }

        taxon = taxon.refresh()
        row.each() {String k, String v ->
          final String provider = (k - "Mapping" - "mapping").trim().intern()
          if (provider != "Internal") {
            if (v) {
              TaxonMapping tm = new TaxonMapping(provider: provider,
                                                 providerTaxonName: v.trim(),
                                                 providerTaxonId: v.trim())
              taxon.addTaxonMapping(tm)
            }
          }
        }
      }
    }
  }
}