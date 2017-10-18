package dinjection.core.components

import org.reflections.Reflections
import org.reflections.util.FilterBuilder
import org.reflections.util.ClasspathHelper
import org.reflections.scanners.ResourcesScanner
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.util.LinkedList


object ComponentSystem {

    private lateinit var components:MutableMap<Class<out Component>, Component>

    fun setup(packages : List<String>) {

        components = mutableMapOf()
        var componentsCollection = mutableSetOf<Class<out Component>>()
        var filterBuilder = FilterBuilder();
        packages.forEach { p -> filterBuilder.includePackage(p) }

        val classLoadersList = LinkedList<ClassLoader>()
        classLoadersList.add(ClasspathHelper.contextClassLoader())
        classLoadersList.add(ClasspathHelper.staticClassLoader())

        val reflections = Reflections(ConfigurationBuilder()
                .setScanners(SubTypesScanner(false /* don't exclude Object.class */), ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(*classLoadersList.toTypedArray()))
                .filterInputsBy(filterBuilder))

        componentsCollection.addAll(reflections.getSubTypesOf(Component::class.java));

        for (c in componentsCollection)
            if (!components.containsKey(c))
                injectComponent(c, componentsCollection, mutableSetOf())

    }

    fun injectComponent(c: Class<out Component>, componentCollection: Set<Class<out Component>>,
                        visitedComponents : MutableSet<Class<out Component>>) {

        if (visitedComponents.contains(c)) {
            throw Exception()
        }
        if (c.constructors.count() > 1)
            throw Exception()

        visitedComponents.add(c);

        val constructor = c.constructors[0];
        val parameters = constructor.parameterTypes

        var arguments = arrayOfNulls<Any>(parameters.size)
        var curArgs = 0;
        for (p in parameters) {
            if (!componentCollection.contains(p)) {
                throw Exception()
            }
            if (!components.containsKey(p)) {
                injectComponent(p as Class<out Component>, componentCollection, visitedComponents)
            }

            var t = components.get(p);
            if (t != null) {
                arguments[curArgs++] = t
            }
        }
        components[c] = constructor.newInstance(*arguments) as Component
    }
}