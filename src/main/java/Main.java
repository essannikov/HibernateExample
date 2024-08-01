import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class Main
{
    public static final String year = "2018";

    public static void main(String[] args) {
        System.out.println("Task:");
        int income = (new Scanner(System.in)).nextInt();

        switch (income){
            case 1:
                //Task1 JDBC
                task1();
                break;

            case 2:
                //Task2 Hibernate connection
                task2();
                break;

            case 3:
                //Task3 Hibernate @Entity
                task3();
                break;

            case 4:
                //Task4 HQL
                task4();
                break;

            default:

        }
    }

    public static void task1(){
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String server = properties.getProperty("url");
        String port = properties.getProperty("port");
        String dbname = properties.getProperty("dbname");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        String url = server + ":" + port + "/" + dbname;

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            String sql = "select sum(count) as count, \n" +
                    "(max(month) - min(month)) as count_month,\n" +
                    "name as name\n" +
                    "from\n" +
                    "(select count(*) as count, \n" +
                    "date_part('month', subscription_date) as month, \n" +
                    "course_name as name\n" +
                    "from \"PurchaseList\"\n" +
                    "where date_part('year', subscription_date) = " + year + "\n" +
                    "group by month, name) as data\n" +
                    "group by name;";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                String rsName = resultSet.getString("name");
                double rsCount = Double.valueOf(resultSet.getString("count"));
                double rsMonth = Double.valueOf(resultSet.getString("count_month"));
                double purchCount = (rsMonth == 0) ? 0: (rsCount / rsMonth);

                System.out.println(rsName + " = " + purchCount);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void task2(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry)
                .getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata
                .getSessionFactoryBuilder().build();


        Session session = sessionFactory.openSession();

        //Select
        Course course = null;
        int id = 0;
        do {
            id++;
            course = session.get(Course.class, id);
            if (course != null) {
                System.out.println(course.getName() + " = " + course.getStudentsCount());
            }
        } while (course != null);

        //Insert
        Transaction transaction = session.beginTransaction();

        course = new Course();
        //course.setId(100); //nextval('"Courses_id_seq"'::regclass)
        course.setName("New course");
        course.setType(CourseType.PROGRAMMING);
        course.setTeacher(session.get(Teacher.class, 2));
        course.setDescription("asd");
        course.setStudentsCount(4);
        course.setDuration(5);
        course.setPrice(99000);
        session.persist(course); //== save(). Метод save() достался нынешнему Hibernate от его предыдущих версий
        //session.save(course);

        transaction.commit();
        //
        session.close();


        sessionFactory.close();
        registry.close();
    }

    public static void task3(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry)
                .getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata
                .getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();

        /*
        Course course = null;
        int id = 0;
        do {
            id++;
            course = session.get(Course.class, id);
            if (course != null) {
                System.out.println(course.getName() +
                        " StudentsCount = " + course.getStudentsCount() +
                        " Teacher = " + course.getTeacher().getName());
                course.getStudents().forEach((student -> {System.out.println(student.getName());}));
            }
        } while (course != null);
        */
        Purchase purchase = new Purchase();
        purchase.setStudent("Бойков Максим");
        purchase.setCourse("Веб-разработчик c 0 до PRO");

        purchase = session.get(Purchase.class, purchase);
        System.out.println(purchase.getStudent() + " / " +
                           purchase.getCourse()  + " / " +
                           purchase.getPrice()   + " / " +
                           purchase.getSubscriptionDate());


        session.close();

        sessionFactory.close();
        registry.close();
    }

    public static void task4(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry)
                .getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata
                .getSessionFactoryBuilder().build();

        Session session = sessionFactory.openSession();

        /*
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Purchase> query = builder.createQuery(Purchase.class);
        Root<Purchase> root = query.from(Purchase.class);
        query.select(root);
        List<Purchase> purchaseList = session.createQuery(query).getResultList();

        purchaseList.forEach(purchase -> System.out.println(purchase.getStudent() + " - " + purchase.getCourse()));
        */
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Student> query = builder.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);
        query.select(root);
        List<Student> students = session.createQuery(query).getResultList();
        Collections.sort(students);

        CriteriaQuery<Course> query2 = builder.createQuery(Course.class);
        Root<Course> root2 = query2.from(Course.class);
        query2.select(root2);
        List<Course> courses = session.createQuery(query2).getResultList();
        Collections.sort(courses);


        String hql = "select s.id , c.id, pl.price, pl.subscriptionDate " +
                "from " + Purchase.class.getSimpleName() + " pl " +
                "join " + Student.class.getSimpleName() + " s on pl.student = s.name " +
                "join " + Course.class.getSimpleName() + " c on pl.course = c.name ";
        List<Object[]> purchaseLinkedsTmp = session.createQuery(hql).getResultList();

        if (purchaseLinkedsTmp != null){
            Transaction transaction = session.beginTransaction();

            for (Object[] line : purchaseLinkedsTmp){
                PurchaseLinked purchaseLinked = new PurchaseLinked();

                Key key = new Key();
                //key.setStudent(session.get(Student.class, line[0])); //Select single
                //key.setCourse(session.get(Course.class, line[1])); //Select single

                Student studentNew = new Student();
                studentNew.setId((Integer) line[0]);
                int i = Collections.binarySearch(students, studentNew);
                if (i < 0){continue;}
                key.setStudent(students.get(i));

                Course courseNew = new Course();
                courseNew.setId((Integer) line[1]);
                i = Collections.binarySearch(courses, courseNew);
                if (i < 0){continue;}
                key.setCourse(courses.get(i));

                purchaseLinked.setId(key);
                purchaseLinked.setPrice((Integer) line[2]);
                purchaseLinked.setSubscriptionDate((Date) line[3]);

                session.persist(purchaseLinked);
            }

            transaction.commit();
        }

        session.close();

        sessionFactory.close();
        registry.close();
    }
}