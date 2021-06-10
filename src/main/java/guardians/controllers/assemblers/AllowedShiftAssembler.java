package guardians.controllers.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import guardians.controllers.AllowedShiftsController;
import guardians.controllers.ShiftConfigurationController;
import guardians.model.dtos.general.AllowedShiftPublicDTO;

/**
 * AllowedShiftAssembler is responsible for converting
 * {@link AllowedShiftPublicDTO}s into their {@link EntityModel} representation.
 * This is, adding the necessary links to them.
 * 
 * @author miggoncan
 */
@Component
public class AllowedShiftAssembler
		implements RepresentationModelAssembler<AllowedShiftPublicDTO, EntityModel<AllowedShiftPublicDTO>> {

	@Value("${api.links.allowedshifts}")
	private String allowedShiftsLink;

	@Value("${api.links.shiftconfs}")
	private String shiftConfsLink;

	@Override
	public EntityModel<AllowedShiftPublicDTO> toModel(AllowedShiftPublicDTO entity) {
		return new EntityModel<AllowedShiftPublicDTO>(entity,
				linkTo(methodOn(AllowedShiftsController.class).getAllowedShift(entity.getId())).withSelfRel(),
				linkTo(methodOn(AllowedShiftsController.class).getAllowedShifts()).withRel(allowedShiftsLink));
	}

	@Override
	public CollectionModel<EntityModel<AllowedShiftPublicDTO>> toCollectionModel(
			Iterable<? extends AllowedShiftPublicDTO> entities) {
		List<EntityModel<AllowedShiftPublicDTO>> allowedShifts = new LinkedList<>();
		for (AllowedShiftPublicDTO entity : entities) {
			allowedShifts.add(this.toModel(entity));
		}
		return new CollectionModel<>(allowedShifts,
				linkTo(methodOn(AllowedShiftsController.class).getAllowedShifts()).withSelfRel(),
				linkTo(methodOn(ShiftConfigurationController.class).getShitfConfigurations()).withRel(shiftConfsLink));
	}

}
