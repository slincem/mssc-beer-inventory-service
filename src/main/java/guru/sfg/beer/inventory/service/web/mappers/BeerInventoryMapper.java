package guru.sfg.beer.inventory.service.web.mappers;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.events.BeerDto;
import guru.sfg.beer.inventory.service.events.BeerInventoryEvent;
import guru.sfg.beer.inventory.service.web.model.BeerInventoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Created by jt on 2019-05-31.
 */
@Mapper(uses = {DateMapper.class})
public interface BeerInventoryMapper {

    BeerInventory beerInventoryDtoToBeerInventory(BeerInventoryDto beerInventoryDTO);

    BeerInventoryDto beerInventoryToBeerInventoryDto(BeerInventory beerInventory);

    @Mapping(target = "beerId", source = "id")
    BeerInventory beerDtoToBeerInventory(BeerDto beerDto);
}
