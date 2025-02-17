package mk.ukim.finki.wp.kol2024g1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class AddOrEditReservation extends AbstractPage {
    private WebElement guestName;
    private WebElement dateCreated;
    private WebElement daysOfStay;
    private WebElement roomType;
    private WebElement hotel;
    private WebElement submit;

    public AddOrEditReservation(WebDriver driver) {
        super(driver);
    }

    public static ItemsPage add(WebDriver driver, String addPath, String guestName, String dateCreated, String daysOfStay, String roomType, String hotel) {
        get(driver, addPath);
        assertRelativeUrl(driver, addPath);

        AddOrEditReservation addOrEditReservation = PageFactory.initElements(driver, AddOrEditReservation.class);
        addOrEditReservation.assertNoError();
        addOrEditReservation.guestName.sendKeys(guestName);
        addOrEditReservation.dateCreated.sendKeys(dateCreated);
        addOrEditReservation.daysOfStay.sendKeys(daysOfStay);

        Select selectType = new Select(addOrEditReservation.roomType);
        selectType.selectByValue(roomType);

        Select selectHotel = new Select(addOrEditReservation.hotel);
        selectHotel.selectByValue(hotel);

        addOrEditReservation.submit.click();
        return PageFactory.initElements(driver, ItemsPage.class);
    }

    public static ItemsPage update(WebDriver driver, WebElement editButton, String guestName, String dateCreated, String daysOfStay, String roomType, String hotel) {
        String href = editButton.getAttribute("href");
        System.out.println(href);
        editButton.click();
        assertAbsoluteUrl(driver, href);

        AddOrEditReservation addOrEditReservation = PageFactory.initElements(driver, AddOrEditReservation.class);
        addOrEditReservation.guestName.clear();
        addOrEditReservation.guestName.sendKeys(guestName);
        addOrEditReservation.dateCreated.clear();
        addOrEditReservation.dateCreated.sendKeys(dateCreated);
        addOrEditReservation.daysOfStay.clear();
        addOrEditReservation.daysOfStay.sendKeys(daysOfStay);

        Select selectType = new Select(addOrEditReservation.roomType);
        selectType.selectByValue(roomType);

        Select selectHotel = new Select(addOrEditReservation.hotel);
        selectHotel.selectByValue(hotel);

        addOrEditReservation.submit.click();
        return PageFactory.initElements(driver, ItemsPage.class);
    }
}