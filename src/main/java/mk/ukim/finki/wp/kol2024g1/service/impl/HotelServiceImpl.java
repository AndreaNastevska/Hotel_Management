package mk.ukim.finki.wp.kol2024g1.service.impl;

import mk.ukim.finki.wp.kol2024g1.model.Hotel;
import mk.ukim.finki.wp.kol2024g1.model.exceptions.InvalidHotelIdException;
import mk.ukim.finki.wp.kol2024g1.repository.HotelRepository;
import mk.ukim.finki.wp.kol2024g1.service.HotelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Hotel findById(Long id) {
        return this.hotelRepository.findById(id).orElseThrow(InvalidHotelIdException::new);
    }

    @Override
    public List<Hotel> listAll() {
        return this.hotelRepository.findAll();
    }

    @Override
    public Hotel create(String name) {
        return this.hotelRepository.save(new Hotel(name));
    }
}
