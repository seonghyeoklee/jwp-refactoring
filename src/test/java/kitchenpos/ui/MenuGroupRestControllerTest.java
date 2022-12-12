package kitchenpos.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitchenpos.application.MenuGroupService;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MenuGroupRestController 테스트")
@WebMvcTest(MenuGroupRestController.class)
public class MenuGroupRestControllerTest {
    @Autowired
    protected MockMvc webMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MenuGroupService menuGroupService;

    private MenuGroup 한마리메뉴_메뉴그룹;
    private MenuGroup 두마리메뉴_메뉴그룹;

    @BeforeEach
    void setUp() {
        한마리메뉴_메뉴그룹 = MenuGroup.of(1L, "한마리메뉴");
        두마리메뉴_메뉴그룹 = MenuGroup.of(2L, "두마리메뉴");
    }

    @DisplayName("메뉴 그룹 등록에 성공한다.")
    @Test
    void 메뉴_등록에_성공한다() throws Exception {
        given(menuGroupService.create(any(MenuGroup.class))).willReturn(한마리메뉴_메뉴그룹);

        webMvc.perform(post("/api/menu-groups")
                        .content(objectMapper.writeValueAsString(한마리메뉴_메뉴그룹))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(한마리메뉴_메뉴그룹.getId().intValue())))
                .andExpect(jsonPath("$.name", is(한마리메뉴_메뉴그룹.getName())));
    }

    @DisplayName("메뉴 그룹 목록을 조회한다.")
    @Test
    void 메뉴_목록을_조회한다() throws Exception {
        given(menuGroupService.list()).willReturn(Arrays.asList(한마리메뉴_메뉴그룹, 두마리메뉴_메뉴그룹));

        webMvc.perform(get("/api/menu-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
}
