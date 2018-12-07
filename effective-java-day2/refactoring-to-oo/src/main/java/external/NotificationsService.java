package external;

import persistence.entities.ShortageEntity;

import java.util.List;

public interface NotificationsService {
    void alertPlanner(List<ShortageEntity> shortages);

    void softNotifyPlanner(List<ShortageEntity> shortages);

    void markOnPlan(List<ShortageEntity> shortages);
}
