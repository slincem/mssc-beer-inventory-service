package guru.sfg.beer.inventory.service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeallocateBeerOrderRequest {

    private BeerOrderDto beerOrderDto;
}
