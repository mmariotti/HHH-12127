package hibernate.test;

import static javax.persistence.criteria.JoinType.LEFT;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import hibernate.model.Authorization;
import hibernate.model.Authorization_;
import hibernate.model.Document;
import hibernate.model.Document_;
import hibernate.model.Person;
import hibernate.model.Person_;


public class EmbeddableQueryTest
{
    // select d from Asset d left join d.authorizations a where a.subject.id=189L

    private static final String Q1 = "select d from Document d left join d.authorizations a where a.person=:person";

    private static final String Q2 = "select d from Document d left join d.authorizations a join a.person p where p=:person";

    private static final String Q3 = "select d from Document d left join d.authorizations a where a.person.id=:personId";

    private static final String Q4 = "select d from Document d left join d.authorizations a join a.person p where p.id=:personId";

    private static EntityManagerFactory emf;

    @Test
    public void testJpql1()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em -> em.createQuery(Q1, Document.class)
            .setParameter("person", p)
            .getResultList());

        assertTrue(resultList.size() == 1);
    }

    @Test
    public void testJpql2()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em -> em.createQuery(Q2, Document.class)
            .setParameter("person", p)
            .getResultList());

        assertTrue(resultList.size() == 1);
    }

    @Test
    public void testJpql3()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em -> em.createQuery(Q3, Document.class)
            .setParameter("personId", p.getId())
            .getResultList());

        assertTrue(resultList.size() == 1);
    }

    @Test
    public void testJpql4()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em -> em.createQuery(Q4, Document.class)
            .setParameter("personId", p.getId())
            .getResultList());

        assertTrue(resultList.size() == 1);
    }


    @Test
    public void testCriteria1()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em ->
        {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Document> query = builder.createQuery(Document.class);

            Root<Document> document = query.from(Document.class);
            SetJoin<Document, Authorization> authorization = document.join(Document_.authorizations, LEFT);

            query.select(document);
            query.where(builder.equal(authorization.get(Authorization_.person), p));

            return em.createQuery(query).getResultList();
        });

        assertTrue(resultList.size() == 1);
    }

    @Test
    public void testCriteria2()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em ->
        {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Document> query = builder.createQuery(Document.class);

            Root<Document> document = query.from(Document.class);
            SetJoin<Document, Authorization> authorization = document.join(Document_.authorizations, LEFT);

            query.select(document);
            query.where(builder.equal(authorization.join(Authorization_.person), p));

            return em.createQuery(query).getResultList();
        });

        assertTrue(resultList.size() == 1);
    }

    @Test
    public void testCriteria3()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em ->
        {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Document> query = builder.createQuery(Document.class);

            Root<Document> document = query.from(Document.class);
            SetJoin<Document, Authorization> authorization = document.join(Document_.authorizations, LEFT);

            query.select(document);
            query.where(builder.equal(authorization.get(Authorization_.person).get(Person_.id), p.getId()));

            return em.createQuery(query).getResultList();
        });

        assertTrue(resultList.size() == 1);
    }

    @Test
    public void testCriteria4()
    {
        Person p = apply(em -> em.find(Person.class, 1L));

        List<Document> resultList = apply(em ->
        {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Document> query = builder.createQuery(Document.class);

            Root<Document> document = query.from(Document.class);
            SetJoin<Document, Authorization> authorization = document.join(Document_.authorizations, LEFT);

            query.select(document);
            query.where(builder.equal(authorization.join(Authorization_.person).get(Person_.id), p.getId()));

            return em.createQuery(query).getResultList();
        });

        assertTrue(resultList.size() == 1);
    }

    @BeforeClass
    public static void init()
    {
        emf = Persistence.createEntityManagerFactory("test");

        accept(em ->
        {
            Person p = new Person();
            p.setId(1L);
            p.setName("person");
            em.persist(p);

            Document d = new Document();
            d.setId(1L);
            d.setName("document");
            d.setResponsible(p);

            Authorization a = new Authorization();
            a.setPerson(p);
            a.setIntervention(true);

            d.getAuthorizations().add(a);

            em.persist(d);
        });
    }

    @AfterClass
    public static void finish()
    {
        if(emf != null)
        {
            emf.close();
        }
    }

    private static void accept(Consumer<EntityManager> consumer)
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            try
            {
                consumer.accept(em);

                tx.commit();
            }
            catch(Exception e)
            {
                tx.rollback();

                throw e;
            }
        }
        finally
        {
            em.close();
        }
    }

    private static <R> R apply(Function<EntityManager, ? extends R> function)
    {
        EntityManager em = emf.createEntityManager();
        try
        {
            EntityTransaction tx = em.getTransaction();
            tx.begin();

            try
            {
                R result = function.apply(em);

                tx.commit();

                return result;
            }
            catch(Exception e)
            {
                tx.rollback();

                throw e;
            }
        }
        finally
        {
            em.close();
        }
    }
}
