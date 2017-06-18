import Ember from 'ember';

export default Ember.Route.extend({
  actions: {
    startBicepCurl() {
      console.log('transitioning to bicep curl');
      this.transitionTo('bicep-curl');
    }
  }
});
