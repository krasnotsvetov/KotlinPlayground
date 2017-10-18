import dinjection.core.PrimitiveLifeTime
import dinjection.core.components.ComponentSystem
import java.io.Closeable

fun main(args : Array<String>) {
    val t = PrimitiveLifeTime().use {
        it.addCloseable(t("hello, world"))
        it.addCloseable(t("bye-bye"))
        ComponentSystem.setup(listOf("dinjection.core"))
    }
}

class t(name:String) : Closeable {
    val Name = name

    override fun close() {
        println(Name)
    }

}

