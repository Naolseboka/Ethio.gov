tasks.register("printPlatformModules") {
    group = "help"
    description = "Lists Phase 0 platform modules"
    doLast {
        subprojects.forEach { println(it.path) }
    }
}
