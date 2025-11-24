case class Employee(name: String, age: Int, salary: Double)

class BankAccount(var balance: Double) {
  def deposit(amount: Double): Unit = balance += amount
  def withdraw(amount: Double): Unit = balance -= amount
  def +(other: BankAccount): BankAccount = new BankAccount(this.balance + other.balance)
}

object BankAccount {
  val minBalance = 0.0
  def apply(balance: Double): BankAccount = new BankAccount(balance)
  def unapply(acc: BankAccount): Option[Double] = Some(acc.balance)
}

object FinanceUtils {
  def yearlyBonus(emp: Employee): Double = emp.salary * 0.10
}

object Main {
  def main(args: Array[String]): Unit = {

    println("Hi")
    val emp1 = Employee("Ritik", 25, 100000)
    val emp2 = emp1.copy(name = "Deepraj", salary = 120000)

    println(emp1)
    println(emp2)

    val account1 = BankAccount(100000.0)
    val account2 = BankAccount(100000.0)

    val account = account1 + account2
    println(account.balance)

    account.deposit(80000.0)
    println(account.balance)

    println(FinanceUtils.yearlyBonus(emp2))

    account match {
      case BankAccount(balance) => println(s"Account balance is $balance")
    }
  }
}
