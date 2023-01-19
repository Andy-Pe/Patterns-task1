package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.delivery.data.DataGenerator.generateDate;

class DeliveryTest {
    String planningDate = generateDate(3);
    private Faker faker;

    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        faker = new Faker(new Locale("ru"));
    }

    @Test
    void shouldTestSuccessfulFormSubmission() {
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(faker.address().city());
        $("[data-test-id=name] input").setValue(faker.name().fullName());
        $("[data-test-id=phone] input").setValue(faker.phoneNumber().phoneNumber());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id=success-notification]").should(appear, Duration.ofSeconds(15));
        $x("//*[contains(text(), 'Встреча успешно запланирована')]")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    void shouldTestIfReplanDateMeeting() {
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = generateDate(daysToAddForSecondMeeting);
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=city] input").setValue("Москва");
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79251112233");
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id=replan-notification]").should(appear, Duration.ofSeconds(15));
        $x("//*[contains(text(), 'другую дату')]").shouldBe(Condition.visible);
        $x("//span[contains(text(), 'Перепланировать')]").click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $("[data-test-id=success-notification]").shouldBe(visible);
    }

    @Test
    void shouldTestIfCheckboxIsEmpty() {
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(planningDate);
        $("[data-test-id=city] input").setValue(faker.address().city());
        $("[data-test-id=name] input").setValue(faker.name().fullName());
        $("[data-test-id=phone] input").setValue(faker.phoneNumber().phoneNumber());
        $x("//*[@class='button__content']").click();
        $("[data-test-id=agreement].input_invalid .checkbox__text")
                .shouldHave(Condition.text("Я соглашаюсь с условиями обработки и использования моих персональных данных"))
                .shouldHave(visible);
    }
}
