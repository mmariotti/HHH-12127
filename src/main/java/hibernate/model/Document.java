package hibernate.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Document implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private long id = System.nanoTime();

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "RESPONSIBLE_ID")
    private Person responsible;

    @ElementCollection
    @CollectionTable(name = "DOCUMENT_AUTHORIZATION", joinColumns = @JoinColumn(name = "DOCUMENT_ID"))
    private Set<Authorization> authorizations = new LinkedHashSet<>();

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Person getResponsible()
    {
        return responsible;
    }

    public void setResponsible(Person responsible)
    {
        this.responsible = responsible;
    }

    public Set<Authorization> getAuthorizations()
    {
        return authorizations;
    }

    public void setAuthorizations(Set<Authorization> authorizations)
    {
        this.authorizations = authorizations;
    }
}
