package mk.ukim.finki.wp.kol2024g1.service;

import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.model.exceptions.InvalidHotelIdException;
import mk.ukim.finki.wp.kol2024g1.model.exceptions.InvalidReservationIdException;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {

    /**
     * @return List of all reservations in the database
     */
    List<Reservation> listAll();

    /**
     * @param id The id of the reservation that we want to obtain
     * @return The reservation with the appropriate id
     * @throws InvalidReservationIdException when there is no reservation with the given id
     */
    Reservation findById(Long id);

    /**
     * This method is used to create a new reservation, and save it in the database.
     *
     * @param guestName
     * @param dateCreated
     * @param daysOfStay
     * @param roomType
     * @param hotelId
     * @return The reservation that is created. The id should be generated when the reservation is created.
     * @throws InvalidHotelIdException when there is no hotel with the given id
     */
    Reservation create(String guestName, LocalDate dateCreated, Integer daysOfStay, RoomType roomType, Long hotelId);

    /**
     * This method is used to update a reservation, and save it in the database.
     *
     * @param id          The id of the reservation that is being updated
     * @param guestName
     * @param dateCreated
     * @param daysOfStay
     * @param roomType
     * @param hotelId
     * @return The reservation that is updated.
     * @throws InvalidReservationIdException when there is no reservation with the given id
     * @throws InvalidHotelIdException       when there is no hotel with the given id
     */
    Reservation update(Long id, String guestName, LocalDate dateCreated, Integer daysOfStay, RoomType roomType, Long hotelId);

    /**
     * @param id
     * @return The reservation that is deleted.
     * @throws InvalidReservationIdException when there is no reservation with the given id
     */
    Reservation delete(Long id);

    /**
     * This method should implement the logic for extending the duration,
     * by adding one day to the daysOfStay.
     *
     * @param id
     * @return The reservation that is extended.
     * @throws InvalidReservationIdException when there is no reservation with the given id
     */
    Reservation extendStay(Long id);

    /**
     * Returns a page of reservations that match the given criteria.
     *
     * @param guestName
     * @param roomType
     * @param hotel
     * @param pageNum
     * @param pageSize
     * @return The page of reservations that match the given criteria.
     */
    Page<Reservation> findPage(String guestName, RoomType roomType, Long hotel, int pageNum, int pageSize);
}
