package dts.logic;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import dts.data.ItemEntity;

public interface ItemDao extends PagingAndSortingRepository<ItemEntity, String> {

	List<ItemEntity> findAllItemsByActive(@Param("areActiveItemsRequired") boolean areActiveItemsRequired,
			Pageable pageable);

	List<ItemEntity> findAllItemsByActiveAndNameLike(@Param("areActiveItemsRequired") boolean areActiveItemsRequired,
			@Param("pattern") String pattern, Pageable pageable);

	List<ItemEntity> findAllItemsByNameLike(@Param("pattern") String pattern, Pageable pageable);

	List<ItemEntity> findAllItemsByTypeLike(@Param("type") String type, Pageable pageable);

	List<ItemEntity> findAllItemsByActiveAndTypeLike(@Param("areActiveItemsRequired") boolean areActiveItemsRequired,
			@Param("type") String type, Pageable pageable);

	List<ItemEntity> findAllByLatGreaterThanEqualAndLatLessThanEqualAndLngGreaterThanEqualAndLngLessThanEqual(
			@Param("minLat") double minLat, @Param("maxLat") double maxLat, @Param("minLng") double minLng,
			@Param("maxLng") double maxLng, Pageable pageable);

	List<ItemEntity> findAllByActiveAndLatGreaterThanEqualAndLatLessThanEqualAndLngGreaterThanEqualAndLngLessThanEqual(
			@Param("areActiveItemsRequired") boolean areActiveItemsRequired, @Param("minLat") double minLat,
			@Param("maxLat") double maxLat, @Param("minLng") double minLng, @Param("maxLng") double maxLng,
			Pageable pageable);

	List<ItemEntity> findAllByParent_spaceItemId(@Param("parentSpaceAndId") String parentSpaceAndId, Pageable pageable);

	List<ItemEntity> findAllByActiveAndParent_spaceItemId(
			@Param("areActiveItemsRequired") boolean areActiveItemsRequired,
			@Param("parentSpaceAndId") String parentSpaceAndId, Pageable pageable);

	List<ItemEntity> findAllByChildren_spaceItemId(@Param("parentSpaceAndId") String parentSpaceAndId,
			Pageable pageable);

	List<ItemEntity> findAllByActiveAndChildren_spaceItemId(
			@Param("areActiveItemsRequired") boolean areActiveItemsRequired,
			@Param("parentSpaceAndId") String parentSpaceAndId, Pageable pageable);

}