import Ember from 'ember';

const {
  Controller,
  computed
} = Ember;

export default Controller.extend({
  maxPitch: 90,
  minPitch: -90,
  minYaw: -90,
  maxYaw: 90,
  minRoll: 0,
  maxRoll: 180,

  satoriManager: Ember.inject.service(),

  computedRoll: computed('satoriManager.currentRoll', 'maxRoll', 'minRoll', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentRoll'), 10);
    if (value < this.get('minRoll')) {
      value = this.get('minRoll');
    }

    if (value > this.get('maxRoll')) {
      value = this.get('maxRoll');
    }

    console.log(`rollValue: ${value}`);

    return Ember.String.htmlSafe(`transform:rotate(${value * -1 + 90}deg)`);
  }),

  computedPitch: computed('satoriManager.currentPitch', 'maxPitch', 'minPitch', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentPitch'), 10);
    if (value < this.get('minPitch')) {
      value = this.get('minPitch');
    }

    if (value > this.get('maxPitch')) {
      value = this.get('maxPitch');
    }

    console.log(`pitchValue: ${value}`);

    return Ember.String.htmlSafe(`transform: rotate(${value * -1}deg)`);
  }),

  computedYaw: computed('satoriManager.currentYaw', 'maxYaw', 'minYaw', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentYaw'), 10);
    if (value < this.get('minYaw')) {
      value = this.get('minYaw');
    }

    if (value > this.get('maxPitch')) {
      value = this.get('maxPitch');
    }

    console.log(`yawValue: ${value}`);

    return Ember.String.htmlSafe(`transform: rotate(${value}deg`);
  }),

  init() {
    this._super();
  }
});
