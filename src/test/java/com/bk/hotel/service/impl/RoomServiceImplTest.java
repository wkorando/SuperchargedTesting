package com.bk.hotel.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.bk.hotel.RoomServiceException;
import com.bk.hotel.model.Room;
import com.bk.hotel.repo.RoomRepo;

public class RoomServiceImplTest {
	private List<String> roomTypes = Arrays.asList("Single", "Double", "Suite");

	@Test
	public void testFindByValidRoomType() {
		// Given
		RoomRepo repo = mock(RoomRepo.class);
		RoomServiceImpl service = new RoomServiceImpl(repo, roomTypes);
		when(repo.findRoomsByRoomType("Single")).thenReturn(Arrays.asList(//
				new Room(1L, "100", "Single", new BigDecimal(145.99))));

		// When
		List<Room> rooms = service.findRoomsByType("Single");

		// Then
		 assertThat(rooms).extracting("roomType").containsExactly("Single");
	}

	@Test
	public void testFindByNullRoomType() {
		// Given
		RoomRepo repo = mock(RoomRepo.class);
		RoomServiceImpl service = new RoomServiceImpl(repo, roomTypes);
		verify(repo, times(0)).findRoomsByRoomType(any());

		// When
		RoomServiceException e = assertThrows(RoomServiceException.class, () -> service.findRoomsByType("NOT FOUND"));

		// Then
		assertThat(e.getMessage()).isEqualTo("Room type: NOT FOUND not found!");
	}

	@Test
	public void testAddRoom() {
		// Given
		RoomRepo repo = mock(RoomRepo.class);
		Room originalRoom = new Room(1L, "100", "Single", new BigDecimal(149.99));
		when(repo.save(any())).thenReturn(originalRoom);
		RoomServiceImpl service = new RoomServiceImpl(repo, roomTypes);

		// When
		Room returnedRoom = service.addRoom(new Room());

		// Then
		assertThat(originalRoom).isEqualToComparingOnlyGivenFields(returnedRoom, "roomType", "roomRate");
	}

	@Test // expected to fail, demonstrating multiple assertion failures
	public void testAddRoomsAll() {
		RoomRepo repo = mock(RoomRepo.class);
		when(repo.save(any())).thenReturn(new Room(1L, "100", "Single", new BigDecimal(149.99)));
		RoomServiceImpl service = new RoomServiceImpl(repo, roomTypes);

		Room newRoom = service.addRoom(new Room());
		assertAll(() -> assertThat(newRoom.getId()).isEqualTo(2L),
				() -> assertThat(newRoom.getRoomNumber()).isEqualTo("200"),
				() -> assertThat(newRoom.getRoomType()).isEqualTo("Dingle"),
				() -> assertThat(newRoom.getRoomRate()).isEqualTo(new BigDecimal(249.99)));

	}

}
