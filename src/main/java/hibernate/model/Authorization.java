package hibernate.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Embeddable
public class Authorization implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "PERSON_ID")
    private Person person;

    private boolean intervention = false;

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }

    public boolean isIntervention()
    {
        return intervention;
    }

    public void setIntervention(boolean intervention)
    {
        this.intervention = intervention;
    }
}
