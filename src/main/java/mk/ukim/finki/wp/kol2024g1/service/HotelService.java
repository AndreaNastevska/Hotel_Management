package mk.ukim.finki.wp.kol2024g1.service;

import mk.ukim.finki.wp.kol2024g1.model.Hotel;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.model.exceptions.InvalidHotelIdException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HotelService {

    /**
     * @param id The id of the hotel that we want to obtain
     * @return The hotel with the appropriate id
     * @throws InvalidHotelIdException when there is no hotel with the given id
     */
    Hotel findById(Long id);

    /**
     * @return List of all hotel in the database
     */
    List<Hotel> listAll();

    /**
     * This method is used to create a new hotel, and save it in the database.
     *
     * @param name
     * @return The hotel that is created. The id should be generated when the hotel is created.
     */
    Hotel create(String name);
}
