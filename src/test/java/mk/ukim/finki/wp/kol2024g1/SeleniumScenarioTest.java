package mk.ukim.finki.wp.kol2024g1;

import com.fasterxml.jackson.core.JsonProcessingException;
import mk.ukim.finki.wp.exam.util.CodeExtractor;
import mk.ukim.finki.wp.exam.util.ExamAssert;
import mk.ukim.finki.wp.exam.util.SubmissionHelper;
import mk.ukim.finki.wp.kol2024g1.model.Hotel;
import mk.ukim.finki.wp.kol2024g1.model.Reservation;
import mk.ukim.finki.wp.kol2024g1.model.RoomType;
import mk.ukim.finki.wp.kol2024g1.service.HotelService;
import mk.ukim.finki.wp.kol2024g1.service.ReservationService;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SeleniumScenarioTest {

    //TODO: CHANGE THE VALUE OF THE SubmissionHelper.index WITH YOUR INDEX NUMBER!!!
    static {
        SubmissionHelper.exam = "exam";
        SubmissionHelper.index = "TODO";
    }

    @Autowired
    HotelService hotelService;

    @Autowired
    ReservationService reservationService;

    @Order(1)
    @Test
    public void test_list_10pt() {
        SubmissionHelper.startTest("test-list-10");
        List<Reservation> items = this.reservationService.listAll();
        int itemNum = items.size();

        ExamAssert.assertNotEquals("Empty db", 0, itemNum);

        ItemsPage listPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, BASE_DEFAULT_URL);
        listPage.assertItems(itemNum);

        SubmissionHelper.endTest();
    }

    @Order(2)
    @Test
    public void test_pagination_10pt() {
        SubmissionHelper.startTest("test-pagination-10");
        int pageSize = 5;
        int pageNum = 1;

        ExamAssert.assertNotEquals("Empty db", 0, pageSize);

        ItemsPage listPage = ItemsPage.to(this.driver, pageSize, pageNum);
        AbstractPage.assertRelativeUrl(this.driver, "/?pageSize=" + pageSize + "&pageNum=" + pageNum);
        listPage.assertItems(pageSize);

        SubmissionHelper.endTest();
    }

    @Order(3)
    @Test
    public void test_filter_5pt() {
        SubmissionHelper.startTest("test-filter-5");
        ItemsPage listPage = ItemsPage.to(this.driver);

        listPage.filter("", "", "");
        listPage.assertItems(10);

        // reset the filter
        listPage = ItemsPage.to(this.driver);

        listPage.filter("2", "", "");
        listPage.assertItems(1);

        listPage = ItemsPage.to(this.driver);

        listPage.filter("", RoomType.SINGLE.name(), "");
        listPage.assertItems(5);

        listPage = ItemsPage.to(this.driver);

        listPage.filter("", "", "2");
        listPage.assertItems(2);

        listPage = ItemsPage.to(this.driver);

        listPage.filter("Reservation", RoomType.SINGLE.name(), "");
        listPage.assertItems(5);

        listPage = ItemsPage.to(this.driver);

        listPage.filter("", RoomType.SINGLE.name(), "2");
        listPage.assertItems(1);

        listPage = ItemsPage.to(this.driver);

        listPage.filter("Reservation", "", "2");
        listPage.assertItems(2);

        listPage = ItemsPage.to(this.driver);

        listPage.filter("Reservation", RoomType.SINGLE.name(), "2");
        listPage.assertItems(1);

        SubmissionHelper.endTest();
    }

    @Order(4)
    @Test
    public void test_filter_service_5pt() {
        SubmissionHelper.startTest("test-filter-service-5");

        ExamAssert.assertEquals("without filter", 10, this.reservationService.findPage(null, null, null, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by guest name only", 1, this.reservationService.findPage("2", null, null, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by roomType only", 5, this.reservationService.findPage(null, RoomType.SINGLE, null, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by hotel only", 2, this.reservationService.findPage(null, null, 2L, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by guest name and room type", 5, this.reservationService.findPage("Reservation", RoomType.SINGLE, null, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by room type and hotel", 1, this.reservationService.findPage(null, RoomType.SINGLE, 2L, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by guest name and hotel", 2, this.reservationService.findPage("Reservation", null, 2L, 0, 20).getNumberOfElements());
        ExamAssert.assertEquals("filter by all", 1, this.reservationService.findPage("Reservation", RoomType.SINGLE, 2L, 0, 20).getNumberOfElements());

        SubmissionHelper.endTest();
    }

    @Order(5)
    @Test
    public void test_create_10pt() {
        SubmissionHelper.startTest("test-create-10");
        List<Hotel> hotels = this.hotelService.listAll();
        List<Reservation> reservations = this.reservationService.listAll();

        int itemNum = reservations.size();
        ItemsPage listPage = null;

        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
        }

        LocalDate date = LocalDate.now().minusYears(30);

        listPage = AddOrEditReservation.add(this.driver, ADD_URL, "testName", date.toString(), "0", RoomType.SINGLE.name(), hotels.get(0).getId().toString());
        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);
        AbstractPage.get(this.driver, LIST_URL);
        listPage.assertNoError();
        listPage.assertItems(itemNum + 1);

        SubmissionHelper.endTest();
    }

    @Order(6)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void test_create_mvc_10pt() throws Exception {
        SubmissionHelper.startTest("test-create-mvc-10");
        List<Hotel> hotels = this.hotelService.listAll();
        List<Reservation> reservations = this.reservationService.listAll();

        int itemNum = reservations.size();

        MockHttpServletRequestBuilder addRequest = MockMvcRequestBuilders
                .post("/reservations")
                .param("guestName", "testName")
                .param("daysOfStay", "0")
                .param("dateCreated", LocalDate.now().minusDays(30).toString())
                .param("roomType", RoomType.SINGLE.name())
                .param("hotelId", hotels.get(0).getId().toString());

        this.mockMvc.perform(addRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(DEFAULT_URL));

        reservations = this.reservationService.listAll();
        ExamAssert.assertEquals("Number of items", itemNum + 1, reservations.size());

        addRequest = MockMvcRequestBuilders
                .get("/reservations/add");

        this.mockMvc.perform(addRequest)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(new ViewMatcher("form")));

        SubmissionHelper.endTest();
    }

    @Order(7)
    @Test
    public void test_edit_10pt() {
        SubmissionHelper.startTest("test-edit-10");
        List<Hotel> hotels = this.hotelService.listAll();
        List<Reservation> reservations = this.reservationService.listAll();

        int itemNum = reservations.size();

        ItemsPage listPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!DEFAULT_URL.equals(this.driver.getCurrentUrl())) {
            System.err.println("Reloading items page");
            listPage = ItemsPage.to(this.driver);
        }

        listPage = AddOrEditReservation.update(this.driver, listPage.getEditButtons().get(itemNum - 1), "testName", LocalDate.now().minusYears(30).toString(), "0", RoomType.SINGLE.name(), hotels.get(0).getId().toString());
        listPage.assertNoError();

        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);
        AbstractPage.get(this.driver, LIST_URL);
        if (listPage.assertItems(itemNum)) {
            ExamAssert.assertEquals("The updated reservation guest name is not as expected.", "testName", listPage.getRows().get(itemNum - 1).findElements(By.tagName("td")).get(0).getText().trim());
        }

        SubmissionHelper.endTest();
    }

    @Order(8)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void test_edit_mvc_10pt() throws Exception {
        SubmissionHelper.startTest("test-edit-mvc-10");
        List<Hotel> hotels = this.hotelService.listAll();
        List<Reservation> reservations = this.reservationService.listAll();

        int itemNum = reservations.size();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/reservations/" + reservations.get(itemNum - 1).getId())
                .param("guestName", "testName")
                .param("dateCreated", LocalDate.now().minusYears(30).toString())
                .param("daysOfStay", "0")
                .param("roomType", RoomType.DOUBLE.name())
                .param("hotelId", hotels.get(0).getId().toString());

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(DEFAULT_URL));

        reservations = this.reservationService.listAll();
        ExamAssert.assertEquals("Number of items", itemNum, reservations.size());
        ExamAssert.assertEquals("The updated reservation guest name is not as expected.", "testName", reservations.get(itemNum - 1).getGuestName());

        request = MockMvcRequestBuilders
                .get("/reservations/edit/" + reservations.get(itemNum - 1).getId());

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(new ViewMatcher("form")));

        SubmissionHelper.endTest();
    }

    @Order(9)
    @Test
    public void test_delete_3pt() throws Exception {
        SubmissionHelper.startTest("test-delete-3");
        List<Reservation> items = this.reservationService.listAll();
        int itemNum = items.size();

        ItemsPage listPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, admin, admin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!DEFAULT_URL.equals(this.driver.getCurrentUrl())) {
            System.err.println("Reloading items page");
            listPage = ItemsPage.to(this.driver);
        }

        listPage.getDeleteButtons().get(itemNum - 1).click();
        listPage.assertNoError();


        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);
        listPage = ItemsPage.to(this.driver);
        listPage.assertItems(itemNum - 1);

        SubmissionHelper.endTest();
    }

    @Order(10)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void test_delete_mvc_2pt() throws Exception {
        SubmissionHelper.startTest("test-delete-mvc-2");
        List<Reservation> items = this.reservationService.listAll();
        int itemNum = items.size();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/reservations/delete/" + items.get(itemNum - 1).getId());

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(DEFAULT_URL));

        items = this.reservationService.listAll();
        ExamAssert.assertEquals("Number of items", itemNum - 1, items.size());

        SubmissionHelper.endTest();
    }

    @Order(11)
    @Test
    public void test_security_urls_10pt() {
        SubmissionHelper.startTest("test-security-urls-10");
        List<Reservation> reservations = this.reservationService.listAll();
        String editUrl = "/reservations/edit/" + reservations.get(0).getId();

        ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, BASE_DEFAULT_URL);

        AbstractPage.get(this.driver, DEFAULT_URL);
        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);
        AbstractPage.get(this.driver, ADD_URL);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, editUrl);
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);
        AbstractPage.get(this.driver, "/random");
        AbstractPage.assertRelativeUrl(this.driver, LOGIN_URL);

        LoginPage loginPage = LoginPage.openLogin(this.driver);
        LoginPage.doLogin(this.driver, loginPage, admin, admin);
        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);

        AbstractPage.get(this.driver, DEFAULT_URL);
        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);

        AbstractPage.get(this.driver, ADD_URL);
        AbstractPage.assertRelativeUrl(this.driver, ADD_URL);

        AbstractPage.get(this.driver, editUrl);
        AbstractPage.assertRelativeUrl(this.driver, editUrl);

        LoginPage.logout(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, "/");

        SubmissionHelper.endTest();
    }

    @Order(12)
    @Test
    public void test_security_buttons_10pt() {
        SubmissionHelper.startTest("test-security-buttons-10");
        List<Reservation> reservations = this.reservationService.listAll();
        int itemNum = reservations.size();

        ItemsPage playersPage = ItemsPage.to(this.driver);
        AbstractPage.assertRelativeUrl(this.driver, BASE_DEFAULT_URL);
        playersPage.assertButtons(0, 0, 0, 0);

        LoginPage loginPage1 = LoginPage.openLogin(this.driver);
        playersPage = LoginPage.doLogin(this.driver, loginPage1, admin, admin);
        playersPage.assertButtons(itemNum, itemNum, 1, 0);
        LoginPage.logout(this.driver);

        LoginPage loginPage2 = LoginPage.openLogin(this.driver);
        playersPage = LoginPage.doLogin(this.driver, loginPage2, user, user);
        playersPage.assertButtons(0, 0, 0, itemNum);
        LoginPage.logout(this.driver);
        SubmissionHelper.endTest();
    }

    @Order(13)
    @Test
    public void test_extend_stay_3pt() throws Exception {
        SubmissionHelper.startTest("test-extend-stay-3");
        List<Reservation> reservations = this.reservationService.listAll();

        int itemNum = reservations.size();

        ItemsPage listPage = null;
        try {
            LoginPage loginPage = LoginPage.openLogin(this.driver);
            listPage = LoginPage.doLogin(this.driver, loginPage, user, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!DEFAULT_URL.equals(this.driver.getCurrentUrl())) {
            System.err.println("Reloading items page");
            listPage = ItemsPage.to(this.driver);
        }

        listPage.getExtendButtons().get(itemNum - 1).click();
        listPage.assertNoError();

        AbstractPage.assertRelativeUrl(this.driver, DEFAULT_URL);
        listPage = ItemsPage.to(this.driver);
        ExamAssert.assertEquals("The updated reservation days of stay are not as expected.", "1", listPage.getRows().get(itemNum - 1).findElements(By.tagName("td")).get(4).getText().trim());

        SubmissionHelper.endTest();
    }

    @Order(14)
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void test_extend_mvc_2pt() throws Exception {
        SubmissionHelper.startTest("test-extend-mvc-2");
        List<Reservation> reservations = this.reservationService.listAll();

        int itemNum = reservations.size();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/reservations/extend/" + reservations.get(0).getId());

        this.mockMvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(DEFAULT_URL));

        reservations = this.reservationService.listAll();
        ExamAssert.assertEquals("Number of days of stay", reservations.get(0).getDaysOfStay(), 1);

        SubmissionHelper.endTest();
    }

    private HtmlUnitDriver driver;
    private MockMvc mockMvc;

    private static String admin = "admin";
    private static String user = "user";

    @BeforeEach
    public void setup(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        this.driver = new HtmlUnitDriver(true);
    }

    @AfterEach
    public void destroy() {
        if (this.driver != null) {
            this.driver.close();
        }
    }

    @AfterAll
    public static void finalizeAndSubmit() throws JsonProcessingException {
        CodeExtractor.submitSourcesAndLogs();
    }

    public static final String LIST_URL = "/reservations?pageSize=20&pageNum=1";
    public static final String DEFAULT_URL = "/reservations";
    public static final String BASE_DEFAULT_URL = "/?pageSize=20&pageNum=1";
    public static final String ADD_URL = "/reservations/add";
    public static final String LOGIN_URL = "/login";

    static class ViewMatcher implements Matcher<String> {

        final String baseName;

        ViewMatcher(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public boolean matches(Object o) {
            if (o instanceof String) {
                String s = (String) o;
                return s.startsWith(baseName);
            }
            return false;
        }

        @Override
        public void describeMismatch(Object o, Description description) {
        }

        @Override
        public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}
