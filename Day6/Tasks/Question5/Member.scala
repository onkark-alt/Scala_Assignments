package library.users {

  import library.items._

  // Member class representing a library user
  class Member(val name: String) {
    def borrowItem(item: ItemType): Unit =
      println(s"$name borrows '${item.title}'")
  }
}