package kitchenpos.application;

import static kitchenpos.utils.generator.MenuGroupFixtureGenerator.generateMenuGroup;
import static kitchenpos.utils.generator.MenuGroupFixtureGenerator.generateMenuGroups;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import kitchenpos.application.menu.MenuGroupService;
import kitchenpos.domain.menu.MenuGroup;
import kitchenpos.domain.menu.MenuGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service:MenuGroup")
class MenuGroupServiceTest {

    @Mock
    private MenuGroupRepository menuGroupRepository;

    @InjectMocks
    private MenuGroupService menuGroupService;

    @Test
    @DisplayName("메뉴 그룹을 생성한다.")
    public void createMenuGroup() {
        // Given
        final MenuGroup given = generateMenuGroup();
        given(menuGroupRepository.save(any(MenuGroup.class))).will(AdditionalAnswers.returnsFirstArg());

        // When
        MenuGroup actual = menuGroupService.create(given);

        // Then
        verify(menuGroupRepository).save(any(MenuGroup.class));
        assertThat(actual).isEqualTo(given);
    }

    @Test
    @DisplayName("메뉴 그룹 목록을 조회한다.")
    public void getMenuGroups() {
        // Given
        final int generateMenuGroupCount = 5;
        List<MenuGroup> givenMenuGroups = generateMenuGroups(generateMenuGroupCount);
        given(menuGroupRepository.findAll()).willReturn(givenMenuGroups);

        // When
        List<MenuGroup> actualMenuGroups = menuGroupService.list();

        // Then
        verify(menuGroupRepository).findAll();
        assertThat(actualMenuGroups).hasSize(generateMenuGroupCount);
    }
}
