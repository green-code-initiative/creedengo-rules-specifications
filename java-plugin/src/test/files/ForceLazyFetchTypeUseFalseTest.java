import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "myTable")
public class ForceLazyFetchTypeUseFalse implements Serializable {

    @Column(name = "ORDER", length = 50, nullable = false, unique = false)
    private Set<OrderItem> items = new HashSet<OrderItem>();

}
