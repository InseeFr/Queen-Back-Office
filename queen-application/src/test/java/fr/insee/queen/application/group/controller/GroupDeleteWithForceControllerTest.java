package fr.insee.queen.application.group.controller;

import fr.insee.queen.application.group.service.dummy.GroupFakeService;
import fr.insee.queen.application.pilotage.controller.dummy.PilotageFakeComponent;
import fr.insee.queen.domain.group.service.exception.GroupDeletionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class GroupDeleteWithForceControllerTest {

    private GroupDeleteWithForceController groupController;
    private PilotageFakeComponent pilotageComponent;
    private GroupFakeService groupService;
    private final String groupId = "11";

    @BeforeEach
    void init() {
        groupService = spy(new GroupFakeService());
        pilotageComponent = new PilotageFakeComponent();
        groupController = new GroupDeleteWithForceController(groupService, pilotageComponent);
    }

    @Test
    @DisplayName("On deletion, when force is true, deletion is done")
    void testDeletion() {
        groupController.deleteGroupById(true, groupId);
        verify(groupService, times(1)).delete(groupId, true);
    }

    @Test
    @DisplayName("On deletion, when group is closed, deletion is done")
    void testDeletion_02() {
        groupController.deleteGroupById(false, groupId);
        verify(groupService, times(1)).delete(groupId, true);
    }

    @Test
    @DisplayName("On deletion, when group is opened, deletion is aborted")
    void testDeletionException() {
        pilotageComponent.setGroupClosed(false);
        assertThatThrownBy(() -> groupController.deleteGroupById(false, groupId))
                .isInstanceOf(GroupDeletionException.class);
        verifyNoInteractions(groupService);
    }


}
