package ru.nsu.zenin.snake.model;

public interface Snake {
    Direction getLastMoveDirection();
    void setPendingDirection(Direction direction);
    void setTicksToMove(Integer ticksToMove);
    Integer getTicksToMove();
    void grow();
    void shrink();
    void tick();

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public Direction getOpposite() {
            return switch (this) {
                case UP -> DOWN;
                case DOWN -> UP;
                case LEFT -> RIGHT;
                case RIGHT -> LEFT;
            };
        }
    }
}
