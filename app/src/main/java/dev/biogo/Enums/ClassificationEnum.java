package dev.biogo.Enums;

public enum ClassificationEnum {
        S(50),
        A(30),
        B(20),
        C(10),
        D(5),
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
