error id: file://<WORKSPACE>/Day5/MultiplicationTable.scala:[234..235) in Input.VirtualFile("file://<WORKSPACE>/Day5/MultiplicationTable.scala", "object MultiplicationTable {
    def multiplicationTable(n: Int): List[String] = {
        val table = for {
            i <-  1 to n
            j <- 1 to n
        } yield s"$i * $j = ${i * j}"

        table.toList
    }

    def 
}")
file://<WORKSPACE>/file:<WORKSPACE>/Day5/MultiplicationTable.scala
file://<WORKSPACE>/Day5/MultiplicationTable.scala:12: error: expected identifier; obtained rbrace
}
^
#### Short summary: 

expected identifier; obtained rbrace