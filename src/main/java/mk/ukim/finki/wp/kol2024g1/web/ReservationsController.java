package mk.ukim.finki.wp.kol2024g1.web;

import jakarta.annotation.PostConstruct;
import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.service.HotelService;
import mk.ukim.finki.wp.kol2024g1.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping({"/reservations", "/"})
public class ReservationsController {

    private final ReservationService reservationService;
    private final HotelService hotelService;

    public ReservationsController(ReservationService reservationService, HotelService hotelService) {
        this.reservationService = reservationService;
        this.hotelService = hotelService;
    }


    @GetMapping
    public String listAll(@RequestParam(required = false) String guestName,
                          @RequestParam(required = false) RoomType roomType,
                          @RequestParam(required = false) Long hotel,
                          @RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          Model model) {
        Page<Reservation> page = this.reservationService.findPage(guestName, roomType, hotel, pageNum - 1, pageSize);
        model.addAttribute("page", page);
        model.addAttribute("roomTypes", Arrays.stream(RoomType.values()).toList());
        model.addAttribute("hotels", this.hotelService.listAll());
        model.addAttribute("guestName", guestName);
        model.addAttribute("roomType", roomType);
        model.addAttribute("hotel", hotel);
        return "list";
    }

    /**
     * This method should display the "form.html" template.
     * The method should be mapped on path '/reservations/add'.
     *
     * @return The view "form.html".
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/add")
    public String showAdd(Model model) {
        model.addAttribute("roomTypes", Arrays.stream(RoomType.values()).toList());
        model.addAttribute("hotels", this.hotelService.listAll());
        return "form";
    }

    /**
     * This method should display the "form.html" template.
     * However, in this case, all 'input' elements should be filled with the appropriate value for the reservations that is updated.
     * The method should be mapped on path '/reservations/edit/[id]'.
     *
     * @return The view "form.html".
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("reservation", this.reservationService.findById(id));
        model.addAttribute("roomTypes", Arrays.stream(RoomType.values()).toList());
        model.addAttribute("hotels", this.hotelService.listAll());
        return "form";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String create(@RequestParam String guestName,
                         @RequestParam LocalDate dateCreated,
                         @RequestParam Integer daysOfStay,
                         @RequestParam RoomType roomType,
                         @RequestParam Long hotelId) {
        this.reservationService.create(guestName, dateCreated, daysOfStay, roomType, hotelId);
        return "redirect:/reservations";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String guestName,
                         @RequestParam LocalDate dateCreated,
                         @RequestParam Integer daysOfStay,
                         @RequestParam RoomType roomType,
                         @RequestParam Long hotelId) {
        this.reservationService.update(id, guestName, dateCreated, daysOfStay, roomType, hotelId);
        return "redirect:/reservations";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        this.reservationService.delete(id);
        return "redirect:/reservations";
    }



    @PreAuthorize("hasRole('USER')")
    @PostMapping("/extend/{id}")
    public String extend(@PathVariable Long id) {
        this.reservationService.extendStay(id);
        return "redirect:/reservations";

    }
}
