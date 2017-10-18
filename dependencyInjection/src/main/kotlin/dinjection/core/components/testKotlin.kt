package dinjection.core.components

import dinjection.core.components.Component

class Test(t:Test2) : Component {
    var Name: String = ""
}


class Test2() : Component {
    var Name: String = "test2"
}