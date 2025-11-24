package library.operations {

  import library.items._
  import library.users._

  object LibraryOps {

    // Implicit default member
    implicit val defaultMember: Member = new Member("Default Member")

    // Implicit conversion from String → Book
    implicit def stringToBook(title: String): Book = Book(title)

    // Borrow method using implicit Member
    def borrow(item: ItemType)(implicit member: Member): Unit = {
      member.borrowItem(item)
    }

    // Describe item using pattern matching
    def itemDescription(item: ItemType): Unit = item match {
      case Book(title)     => println(s"Book: $title")
      case Magazine(title) => println(s"Magazine: $title")
      case DVD(title)      => println(s"DVD: $title")
    }
  }
}