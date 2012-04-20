grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
  // inherit Grails' default dependencies
  inherits("global") {
    // uncomment to disable ehcache
    // excludes 'ehcache'
  }
  log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
  checksums true // Whether to verify checksums on resolve

  repositories {
    inherits true // Whether to inherit repository definitions from plugins
    mavenLocal()
    grailsPlugins()
    grailsHome()
    grailsCentral()
    mavenCentral()

    // uncomment these to enable remote dependency resolution from public Maven repositories
    //mavenCentral()
    //mavenRepo "http://snapshots.repository.codehaus.org"
    //mavenRepo "http://repository.codehaus.org"
    //mavenRepo "http://download.java.net/maven/2/"
    //mavenRepo "http://repository.jboss.com/maven2/"
    mavenRepo "http://oss.sonatype.org/content/repositories/releases"
    mavenRepo "http://m2repo.spockframework.org/snapshots/"
    mavenRepo "http://repo.aduna-software.org/maven2/releases/"

  }
  dependencies {
    // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

//    def ses = "2.6.4"
//    compile("org.openrdf.sesame:sesame:${ses}",
//            "org.openrdf.sesame:sesame-sail:${ses}",
//            "org.openrdf.sesame:sesame-repository-api:${ses}",
//            "org.openrdf.sesame:sesame-core:${ses}",
//            "org.openrdf.sesame:sesame-config:${ses}",
//            "org.openrdf.sesame:sesame-model:${ses}",
//            "org.openrdf.sesame:sesame-query:${ses}",
//            "org.openrdf.sesame:sesame-queryparser-api:${ses}",
//            "org.openrdf.sesame:sesame-queryresultio-api:${ses}",
//            "org.openrdf.sesame:sesame-repository-event:${ses}",
//            "org.openrdf.sesame:sesame-repository-sail:${ses}",
//            "org.openrdf.sesame:sesame-util:${ses}",
//            "org.openrdf.sesame:sesame-sail-memory:${ses}",
//            "org.openrdf.sesame:sesame-repository-dataset:${ses}",
//            "org.openrdf.sesame:sesame-repository-contextaware:${ses}",
//            //             "org.openrdf.sesame:sesame-rio-binary:${ses}",
//            //             "org.openrdf.sesame:sesame-rio-turtle:${ses}",
//            //             "org.openrdf.sesame:sesame-sail-rdbms:${ses}",
//            "org.openrdf.sesame:sesame-runtime:${ses}") {
//      excludes "logback-classic", "stax-api", "logback-core",
//               "commons-lang", "commons-beanutils", "httpclient",
//               "commons-codec", "slf4j-api"
//    }
    runtime("com.google.guava:guava:11.0.1")
    //    runtime("org.99soft.semweb:sameas4j:2.0",
    //            "com.google.code.gson:gson:1.6+") {
    //      excludes "stax-api"
    //    }
    runtime("org.codehaus.jackson:jackson-mapper-asl:1.9.5",
            "org.codehaus.jackson:jackson-jaxrs:1.9.5") {
      excludes "stax-api"
    }

    runtime('com.amazonaws:aws-java-sdk:1.3.3') {
      transitive = false
      excludes "stax-api", "stax"
    }

    def camelVer = "2.9.1"
    runtime("org.apache.camel:camel-aws:${camelVer}") {
      excludes "aws-java-sdk", "jackson-core-asl",
               "commons-codec", "stax-api"
    }

    runtime("com.factual:factual-java-driver:1.1.0+") {
      excludes "jackson", "stax-api", "jackson-core-asl",
               "guava", "jackson-mapper-asl", "commons-codec",
               "httpclient", "jackson-core-lgpl", "xpp3", "junit"
    }

    compile("org.codehaus.gpars:gpars:latest.release")
  }

  plugins {
    runtime ":hibernate:$grailsVersion"
    runtime ":jquery:1.7.1+"
    runtime ":resources:1.1.6+"
    test ":spock:0.6"
    runtime(":mongodb:1.0.0.RC4") {
      excludes "spring-core", "spring-beans",
               "spring-context", "spring-tx",
               "commons-collections", "spring-web",
               "grails-datastore-gorm", "grails-bootstrap", "slf4j-api"
    }
    runtime(":csv:0.3.1+")
    runtime(":routing:1.2.0+"){
      excludes "slf4j-api"
    }


    // Uncomment these (or add new ones) to enable additional resources capabilities
    //runtime ":zipped-resources:1.0"
    //runtime ":cached-resources:1.0"
    //runtime ":yui-minify-resources:0.1.4"

    build ":tomcat:$grailsVersion"
  }
}
