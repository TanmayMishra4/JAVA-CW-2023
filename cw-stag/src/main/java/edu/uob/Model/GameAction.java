package edu.uob.Model;

import java.util.HashSet;

public class GameAction {
    private HashSet<String> triggers;
    private HashSet<GameEntity> subjects;
    private HashSet<GameEntity> consumed;

    public void setTriggers(HashSet<String> triggers) {
        this.triggers = triggers;
    }

    public void setSubjects(HashSet<GameEntity> subjects) {
        this.subjects = subjects;
    }

    public void setConsumed(HashSet<GameEntity> consumed) {
        this.consumed = consumed;
    }

    public void setProduced(HashSet<GameEntity> produced) {
        this.produced = produced;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public HashSet<String> getTriggers() {
        return triggers;
    }

    public HashSet<GameEntity> getSubjects() {
        return subjects;
    }

    public HashSet<GameEntity> getConsumed() {
        return consumed;
    }

    public HashSet<GameEntity> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

    private HashSet<GameEntity> produced;
    private String narration;



    public static class GameActionBuilder {
        private HashSet<String> triggers;
        private HashSet<GameEntity> subjects;
        private HashSet<GameEntity> consumed;
        private HashSet<GameEntity> produced;
        private String narration;

        public GameActionBuilder(String narration, HashSet<String> triggers, HashSet<GameEntity> subjects) {
            this.triggers = triggers;
            this.subjects = subjects;
            this.narration = narration;
            consumed = new HashSet<>();
            produced = new HashSet<>();
        }

        public GameActionBuilder setConsumed(HashSet<GameEntity> consumed) {
            this.consumed = consumed;
            return this;
        }

        public GameActionBuilder setProduced(HashSet<GameEntity> produced) {
            this.produced = produced;
            return this;
        }

        public GameActionBuilder setNarration(String narration) {
            this.narration = narration;
            return this;
        }

        public GameAction build(){
            GameAction gameAction = new GameAction();
            gameAction.setNarration(narration);
            gameAction.setConsumed(consumed);
            gameAction.setProduced(produced);
            gameAction.setSubjects(subjects);
            gameAction.setTriggers(triggers);
            return gameAction;
        }
    }
}
