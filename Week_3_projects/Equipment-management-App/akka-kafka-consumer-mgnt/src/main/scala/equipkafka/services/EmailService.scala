package equipkafka.services

import jakarta.mail._
import jakarta.mail.internet._
import java.util.Properties

class EmailService {

  private val props = new Properties()

  props.put("mail.smtp.auth", "true")
  props.put("mail.smtp.starttls.enable", "true")
  props.put("mail.smtp.host", "smtp.gmail.com")
  props.put("mail.smtp.port", "587")

  private val username = "onkar.vallal.9@gmail.com"
  private val password = "pucxcszckjgzgsaq"

  private val session = Session.getInstance(props, new Authenticator() {
    override protected def getPasswordAuthentication =
      new PasswordAuthentication(username, password)
  })

  def sendEmail(to: String, subject: String, body: String): Unit = {
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(username))
    message.setRecipients(Message.RecipientType.TO, to)
    message.setSubject(subject)
    message.setText(body)

    Transport.send(message)
  }
}
