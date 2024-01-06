package guru.sfg.beer.inventory.service.events;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NewInventoryEvent extends BeerInventoryEvent {

    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
