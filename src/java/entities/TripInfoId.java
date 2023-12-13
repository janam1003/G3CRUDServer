package entities;

import java.io.Serializable;
import javax.persistence.Embeddable;
/**
 * Entity for TripInfoId, which is the id for TripInfo
 * @author Iñigo
 */
@Embeddable
public class TripInfoId implements Serializable{
	private Integer tripId;
	private Integer customerId;
}
