package dev.biogo.Enums;

public enum ClassificationEnum {
        S(50),
        A(30),
        B(20),
        C(10),
        D(5),
        PENDING(0),
        NOT_VALID(-1);


        private int xp;
        ClassificationEnum(int xp){
                this.xp = xp;
        }
        public int getValue(){return this.xp;}


}
