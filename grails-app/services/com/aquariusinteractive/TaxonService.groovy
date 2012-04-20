package com.aquariusinteractive

import com.aquariusinteractive.content.Taxon
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.Assert


class TaxonService implements InitializingBean {

  static             transactional = 'mongo'

  /**
   * Add an taxonomy leaf to the taxon db.
   * Handles parent->child traversal and node
   * creation.
   *
   * @param leaf ["Parent", "Child1", ChildNN"]
   */
  void processLeaf(final List<String> leaf) {


    leaf.eachWithIndex() { String name, int i ->
      String path
      Taxon node
      if (i == 0) {
        path = name
        Taxon.withNewSession {
          node = getRoot([ name: name,
                                 fullPath: path ])
          node.save()
        }
        Assert.isTrue((Boolean) node, "Root node unresolved")
      } else {

        path = leaf.subList(0, i + 1).join(' > ')
        node = getParent([ name: leaf[ i - 1 ] ])

        if (!node.getChildren().find
                { Taxon it -> it.name == name }) {
          Taxon.withNewSession {
            Taxon child = new Taxon(name: name, fullPath: path)
            node.addToChildren(child)
          }
        }
        Assert.isTrue((Boolean) node, "Node is missing.")
      }
    }
  }

  private Taxon getParent(final Map args) {
    Taxon.withNewSession {
      Taxon t = Taxon.findByName(args[ 'name' ] as String)
      if (!t) {
        throw new RuntimeException("Parent node appears to be missing.\nargs passed: ${args}")
      }
      return t
    }
  }

  private Taxon getRoot(final Map args) {
    log.trace("getRoot called ${args}")

    Taxon t = Taxon.findByName(args[ 'name' ] as String)

    if (!t) {
      t = new Taxon(args)
    }
    return t
  }

  public Taxon getOrCreateTaxonByPathArray(final List pathArray, boolean create) {
    final String path = pathArray.join(' > ').intern()
    Taxon t = Taxon.findByFullPath(path)
    if (!t && create) {
      processLeaf(pathArray)
      t = Taxon.findByFullPath(path)
    }
    return t
  }

  public Taxon getTaxon(Map args) {
    return Taxon.find(args)
  }


  public List<Taxon> searchTaxonsByPrefix(String prefix) {

    List<Taxon> results = [ ]
    def criteria = Taxon.createCriteria()
    results = criteria.list {
      or {
        ilike('name', "${prefix}%")
        ilike('name', "%${prefix}%")
      }
    }
    return results
  }


  @Override
  void afterPropertiesSet() {

  }
}
