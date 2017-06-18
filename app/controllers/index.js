import Ember from 'ember';

const {
  A,
  Controller,
  computed,
  get,
  set
} = Ember;

export default Controller.extend({
  currentIndex: 0,
  exercises: null,

  currentExercise: computed('exercises', 'currentIndex', function() {
    return get(this, 'exercises').objectAt(get(this, 'currentIndex'));
  }),

  init() {
    this._super();
    set(this, 'exercises', A([
      {
        name: 'Bench Press',
        image: 'http://nutribody.com/wp-content/uploads/2015/10/bench-press.jpg',
        description: 'Overhead press, keep that bar straight!'
      },
      {
        name: 'Seated Row',
        image: 'somesource.com',
        description: 'Practice pulling straight back to focus on your back muscles.'
      },
      {
        name: 'Bicep Curl',
        image: 'somesource.com',
        description: 'Practice the perfect bicep curl arch, all the way up and all the way down.'
      }
    ]));
  },

  actions: {
    goToExercise() {
      this.transitionToRoute('calibration');
    },

    nextExercise() {
      let currentIndex = get(this, 'currentIndex');
      let numExercises = get(this, 'exercises.length');
      this.incrementProperty('currentIndex');
      if (currentIndex >= numExercises - 1) {
        set(this, 'currentIndex', 0);
      }
    },

    previousExercise() {
      let currentIndex = get(this, 'currentIndex');
      this.decrementProperty('currentIndex');

      if (currentIndex < 0) {
        let numExercises = get(this, 'exercises.length');
        set(this, 'currentIndex', numExercises);
      }
    }
  }
});
