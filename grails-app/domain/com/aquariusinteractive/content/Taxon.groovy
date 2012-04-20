package com.aquariusinteractive.content

import com.google.common.base.Objects
import groovy.util.logging.Log4j
import org.apache.commons.lang.builder.CompareToBuilder
import org.springframework.validation.ObjectError

@Log4j
class Taxon implements Serializable, Comparable {

  String         id
  String         name
  String         description
  Set<Map>       flags
  String         fullPath
          String parent
  private Set    children

  static mapWith    = "mongo"
  static transients = ['children']

  static constraints = {
    name index: true, nullable: false
    fullPath index: true, unique: true
    description nullable: true
    parent index: true, nullable: true
  }

  static mapping = {
    version(false)
  }

  /**
   * TODO THIS IS NOT BEING FIRED
   */
  def beforeDelete() {
    log.error("Before Delete Called")
    final List tm
    TaxonMapping.withNewSession {
      tm = TaxonMapping.findAllByInternalTaxon(this)
    }
    final List t = getChildren()
    if (tm || t) {
      final String error = """Error.  Taxon ${this.fullPath}
      has (${tm.size()}) TaxonMapping entries and/or (${t.size()})
      children.  Cannot proceed."""
      final ObjectError oe = new ObjectError(this.fullPath, error)
      this.errors.globalErrors << oe
      log.error(oe)
      return false
    } else {
      return true
    }
  }

  /**
   * Add Child Taxon to this parent
   * @param child
   */
  public void addToChildren(Taxon _taxon) {

    Taxon.withNewSession {
      _taxon.attach()
      _taxon.parent = this.id
      _taxon.save()
    }
  }

  /**
   * Delete child Taxon from this parent
   * @param Taxon
   */
  public void deleteFromChildren(Taxon taxon) {
    if (taxon.parent != this.id) {
      throw new RuntimeException('''Child is not associated
with this parent.''')
    }
    Taxon.withNewSession {
      taxon.delete()
    }
  }

  /**
   * Retrieve a Set of childrent associated with this
   * parent taxon.
   * @return Set of children
   */
  public Set<Taxon> getChildren() {
    Set kids = new TreeSet()
    Taxon.withNewSession {
      kids = Taxon.findAllByParent(this.id)
    }
    return kids
  }

  /**
   * Get the Taxon mapping equivalent for this
   * taxon.  Used when resolving a category taxon
   * consumed in a provider API call to map to internal
   * representation of that category.
   *
   * @param provider name
   * @return TaxonMapping
   */
  public TaxonMapping getTaxonMappingForProvider(String provider) {
    return TaxonMapping.find([provider: provider, internalTaxon: this])
  }

  /**
   * Get all TaxonMappings for this taxon. Returns
   * all providers with an equivalent mapping to this
   * taxon.
   * @return List of mappings
   */
  public List<TaxonMapping> getTaxonMappings() {
    return TaxonMapping.findAll([internalTaxon: this])
  }

  /**
   * Add a Taxon Mapping for this taxon.  Taxon Mappings
   * are used to identify equivalence across provider
   * API's to internal Taxonomy.
   *
   * @param prototype
   */
  public void addTaxonMapping(TaxonMapping prototype) {
    if (!prototype.internalTaxon) {
      prototype.internalTaxon = this
    }
    if (!TaxonMapping.find(prototype)) {
      prototype.save(flush: true, failOnError: true)
    }
  }

  /**
   * Delete a mapping
   * @param prototype
   */
  public void deleteTaxonMapping(TaxonMapping prototype) {
    if (!prototype.internalTaxon == this) {
      throw new RuntimeException("""This TaxonMapping is not
associated with ${this.fullPath}.""")
    }
    prototype.delete(flush: true)
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
                  .add('name', name)
                  .add('fullPath', fullPath)
                  .add('children count', getChildren().size())
                  .add('parent', parent)
                  .toString()
  }

  @Override
  boolean equals(o) {
    if (this.is(o)) return true
    if (getClass() != o.class) return false

    Taxon taxon = (Taxon) o

    if (fullPath != taxon.fullPath) return false
    if (name != taxon.name) return false
    if (parent != taxon.parent) return false

    return true
  }

  @Override
  int hashCode() {
    int result
    result = (name != null ? name.hashCode() : 0)
    result = 31 * result + (fullPath != null ? fullPath.hashCode() : 0)
    result = 31 * result + (parent != null ? parent.hashCode() : 0)
    return result
  }


  @Override
  int compareTo(Object t) {
    Taxon other = (Taxon) t
    return new CompareToBuilder()
            .append(this.name, other.name)
            .append(this.fullPath, other.fullPath)
            .append(this.parent, other.parent)
            .toComparison()
  }

}
