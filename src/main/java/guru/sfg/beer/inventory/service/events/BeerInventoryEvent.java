package guru.sfg.beer.inventory.service.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerInventoryEvent implements Serializable {

    static final long serialVersionUID = 5412469834960308178L;

    private BeerDto beerDto;

}
