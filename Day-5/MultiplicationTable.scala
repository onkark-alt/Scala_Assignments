object MultiplicationTable {
    def multiplicationTable(n: Int): List[String] = {
        val table = for {
            i <-  1 to n
            j <- 1 to n
        } yield s"$i * $j = ${i * j}"

        table.toList
    }

    def main(args: Array[String]): Unit = {
        val table = multiplicationTable(3);
        table.foreach(println)
    }
}