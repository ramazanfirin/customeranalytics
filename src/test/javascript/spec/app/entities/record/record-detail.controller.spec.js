'use strict';

describe('Controller Tests', function() {

    describe('Record Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockRecord, MockDevice, MockStuff;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockRecord = jasmine.createSpy('MockRecord');
            MockDevice = jasmine.createSpy('MockDevice');
            MockStuff = jasmine.createSpy('MockStuff');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Record': MockRecord,
                'Device': MockDevice,
                'Stuff': MockStuff
            };
            createController = function() {
                $injector.get('$controller')("RecordDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'customeranalyticsApp:recordUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
