import Ember from 'ember';

const {
  Controller,
  computed
} = Ember;

export default Controller.extend({
  maxPitch: 90,
  minPitch: -90,

  satoriManager: Ember.inject.service(),

  computedPitch: computed('satoriManager.currentPitch', 'maxPitch', 'minPitch', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentPitch'), 10);
    if (value < this.get('minPitch')) {
      value = this.get('minPitch');
    }

    if (value > this.get('maxPitch')) {
      value = this.get('maxPitch');
    }

    return Ember.String.htmlSafe(`transform: rotate(${value * -1}deg)`);
  }),

  init() {
    this._super();
  }
});
