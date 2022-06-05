package woowacourse.shoppingcart.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import woowacourse.shoppingcart.dto.ChangePasswordRequest;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;
import woowacourse.shoppingcart.exception.DuplicateCustomerException;
import woowacourse.shoppingcart.exception.IncorrectPasswordException;
import woowacourse.shoppingcart.exception.InvalidCustomerException;

@SpringBootTest
@Sql("/truncate.sql")
class CustomerServiceTest {

    private final CustomerService customerService;

    @Autowired
    public CustomerServiceTest(CustomerService customerService) {
        this.customerService = customerService;
    }

    @DisplayName("Customer 정보를 저장할 수 있다.")
    @Test
    public void save() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");

        // when
        customerService.save(request);

        // then
        final CustomerResponse response = customerService.findByEmail(request.getEmail());
        assertThat(response)
            .extracting("email", "username")
            .contains(request.getEmail(), request.getUsername());
    }

    @DisplayName("이미 존재하는 email로는 회원가입을 할 수 없다.")
    @Test
    public void saveDuplicateEmail() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");

        // when
        customerService.save(request);

        // then
        assertThatThrownBy(() -> customerService.save(request)).isInstanceOf(DuplicateCustomerException.class);
    }

    @DisplayName("email 을 통해서 해당 Customer를 조회할 수 있다.")
    @Test
    public void findByEmail() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");
        customerService.save(request);

        // when
        final CustomerResponse response = customerService.findByEmail(request.getEmail());

        // then
        assertThat(response)
            .extracting("email", "username")
            .contains(request.getEmail(), request.getUsername());
    }

    @DisplayName("email과 함께 기존 비밀번호가 일치하면 새로운 비밀번호로 변경할 수 있다.")
    @Test
    public void changePassword() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");
        customerService.save(request);

        final ChangePasswordRequest changePasswordRequest =
            new ChangePasswordRequest("password1!", "password2!");

        // when & then
        assertDoesNotThrow(() -> customerService.changePassword(request.getEmail(), changePasswordRequest));
    }

    @DisplayName("email과 함께 기존 비밀번호가 일치하지 않으면 새로운 비밀번호로 변경할 수 없다.")
    @Test
    public void failChangePassword() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");
        customerService.save(request);

        final ChangePasswordRequest changePasswordRequest =
            new ChangePasswordRequest("password1!!", "password2!");

        // when & then
        assertThatThrownBy(() -> customerService.changePassword(request.getEmail(), changePasswordRequest))
            .isInstanceOf(IncorrectPasswordException.class);
    }

    @DisplayName("email 을 기준으로 customer 정보를 업데이트할 수 있다.")
    @Test
    public void updateCustomer() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");
        customerService.save(request);

        // when
        final CustomerResponse response = customerService.update(request.getEmail(), "dwoo");

        // then
        assertThat(response)
            .extracting("email", "username")
            .containsExactly("email@email.com", "dwoo");
    }

    @DisplayName("비밀번호가 일치하면 회원정보를 삭제할 수 있다.")
    @Test
    public void delete() {
        // given
        final CustomerRequest request =
            new CustomerRequest("email@email.com", "password1!", "azpi");
        customerService.save(request);

        // when
        customerService.delete(request.getEmail(), request.getPassword());

        // then
        assertThatThrownBy(() -> customerService.findByEmail(request.getEmail()))
            .isInstanceOf(InvalidCustomerException.class);
    }
}