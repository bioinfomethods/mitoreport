package mitoreport

trait ToMap {

    def toMap() {
        def result = [:]
        def properties = this.metaClass.getProperties().findAll { it.name != "class" && it.type != Closure }

        properties.each {
            def propValue = this.metaClass.getProperty(this, it.name)
            if (propValue instanceof ToMap) {
                result[(it.name)] = propValue.toMap()
            } else {
                result[(it.name)] = propValue
            }

        }

        return result
    }
}
