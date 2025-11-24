package main {

  import library.items._
  import library.users._
  import library.operations.LibraryOps._

  object LibraryMain extends App {

    println("\n=== Library Management System ===")

    // Explicit member
    val alice = new Member("Alice")
    val book1: Book = Book("Scala Programming")
    borrow(book1)(using alice) 

  
    val dvd1: DVD = DVD("Inception")
    borrow(dvd1) 

 
    borrow("Harry Potter") 

    println("\n=== Item Descriptions ===")
    val items: List[ItemType] = List(
      Book("FP in Scala"),
      Magazine("Science Today"),
      DVD("Matrix")
    )
    items.foreach(itemDescription)
  }
}