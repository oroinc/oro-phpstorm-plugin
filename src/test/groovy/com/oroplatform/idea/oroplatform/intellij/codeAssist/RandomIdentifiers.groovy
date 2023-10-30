package groovy.com.oroplatform.idea.oroplatform.intellij.codeAssist


trait RandomIdentifiers {
    def String randomIdentifier(String prefix = "") {
        Random random = new Random()

        def numberOfLetters = random.nextInt(20) + 3
        def word = (0..numberOfLetters)
            .collect { random.nextInt(('z' as char) - ('a' as char)) }
            .collect { (it.intValue() + ('a' as char)) as char }
            .join("")

        return prefix + "_" + word + Math.abs(random.nextInt())
    }

    def insertSomewhere(String s, String what) {
        return s.substring(0, 3) + what + s.substring(3)
    }
}