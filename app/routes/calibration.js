import Ember from 'ember';

export default Ember.Route.extend({
  actions: {
    startBicepCurl() {
      this.transitionTo('bicep-curl');
    }
  }
});
