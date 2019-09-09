package com.cugb.talkhub.community.Service;

import com.cugb.talkhub.community.dto.NotificationDTO;
import com.cugb.talkhub.community.dto.PaginationDTO;
import com.cugb.talkhub.community.enums.NotificationStatusEnum;
import com.cugb.talkhub.community.enums.NotificationTypeEnum;
import com.cugb.talkhub.community.exception.CustomizeErrorCode;
import com.cugb.talkhub.community.exception.CustomizeException;
import com.cugb.talkhub.community.mapper.NotificationMapper;
import com.cugb.talkhub.community.model.Notification;
import com.cugb.talkhub.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    public PaginationDTO list(Integer id, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        Integer totalPage;
        Integer totalCount = notificationMapper.countByUserId(id);
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        List<Notification> notifications = notificationMapper.listByUserId(id, offset, size);

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Integer unreadCount(Integer id) {
        return notificationMapper.countByUserAndStatus(id);
    }

    /**
     * 标记问题或评论为已读
     * @param id
     * @param user
     * @return
     */
    public NotificationDTO read(Integer id, User user) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateStatus(NotificationStatusEnum.READ.getStatus(),id);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
