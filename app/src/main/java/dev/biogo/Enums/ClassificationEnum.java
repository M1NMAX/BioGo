package dev.biogo.Enums;

public enum ClassificationEnum {
        VALID(0),
        PENDING(0),
        INVALID(-1);


        private int xp;
        private float color;
        ClassificationEnum(int xp){
                this.xp = xp;
        }
        ClassificationEnum(float color) {this.color = color;}
        public int getValue(){return this.xp;}


}
