package in.tech_camp.chat_app.entity;

import java.util.List;

import lombok.Data;

@Data
public class RoomUserEntity {
  private Long id;
  private UserEntity user;
  private RoomEntity room;
  private List<RoomUserEntity> roomUsers;
}
