package awabi2048.rift_dungeons.generation

enum class StructureType {
    T_JUNCTION, // T が 初期位置
    CROSSROADS, // 十 が 初期位置
    L_CORNER, // L が 初期位置
    STRAIGHT, // | が 初期位置
    CLOSURE, // 一番上が出てるものが初期位置
    START,
}
