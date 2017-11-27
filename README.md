# HHH-12127
org.hibernate.QueryException: could not resolve property: person.id

a simple PoC that:

    select d from Document d left join d.authorizations a where a.person.id=:personId
    
where Document.authorizations is an @ElementCollection, throws an exception.


run with:

     mvn test 
