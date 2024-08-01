import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "\"PurchaseList\"", schema = "PUBLIC")
public class Purchase
{
    @Id
    //@ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "student_name")
    private String student;

    @Id
    //@ManyToOne(cascade = CascadeType.ALL)
    @Column(name = "course_name")
    private String course;

    private int price;

    @Column(name = "subscription_date")
    private Date subscriptionDate;

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}
